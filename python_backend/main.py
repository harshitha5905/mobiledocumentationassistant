import os
from fastapi import FastAPI, UploadFile, File, HTTPException
from pydantic import BaseModel
from typing import List

from document_processor import extract_text_from_pdf, chunk_text
from embeddings import generate_embeddings, generate_query_embedding
from vector_store import vector_store_instance
from rag import generate_answer

app = FastAPI(title="Mobile Document Assistant Backend")

class AskRequest(BaseModel):
    question: str

class AskResponse(BaseModel):
    answer: str
    retrieved_chunks: List[str]

@app.get("/health")
def health_check():
    return {"status": "healthy"}

@app.post("/upload")
async def upload_document(file: UploadFile = File(...)):
    if not file.filename.lower().endswith('.pdf'):
        raise HTTPException(status_code=400, detail="Only PDF files are supported.")
    
    # Save the file temporarily
    temp_path = f"temp_{file.filename}"
    try:
        with open(temp_path, "wb") as f:
            content = await file.read()
            f.write(content)
        
        # Extract text
        text = extract_text_from_pdf(temp_path)
        if not text.strip():
            raise HTTPException(status_code=400, detail="The selected PDF is empty or text extraction failed.")
            
        # Divide into chunks
        chunks = chunk_text(text)
        
        # Generate embeddings
        embeddings = generate_embeddings(chunks)
        
        # Store in FAISS
        vector_store_instance.clear()
        vector_store_instance.add_documents(chunks, embeddings)
        
        return {"filename": file.filename, "message": "PDF processed and indexed successfully."}
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        if os.path.exists(temp_path):
            os.remove(temp_path)

@app.post("/ask", response_model=AskResponse)
def ask_question(request: AskRequest):
    if not request.question.strip():
        raise HTTPException(status_code=400, detail="Question cannot be empty.")
        
    try:
        # Convert question to embedding
        query_embedding = generate_query_embedding(request.question)
        
        # Retrieve chunks from FAISS
        retrieved_chunks = vector_store_instance.search(query_embedding, k=3)
        
        # Generate answer using RAG logic
        answer = generate_answer(request.question, retrieved_chunks)
        
        return AskResponse(answer=answer, retrieved_chunks=retrieved_chunks)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
