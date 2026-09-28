import fitz  # PyMuPDF

def extract_text_from_pdf(file_path):
    """Extracts text from a PDF file."""
    text = ""
    try:
        with fitz.open(file_path) as doc:
            for page in doc:
                text += page.get_text()
    except Exception as e:
        print(f"Error extracting text: {e}")
    return text

def chunk_text(text, chunk_size=500, overlap=50):
    """Divides text into smaller chunks with overlap."""
    chunks = []
    if not text:
        return chunks
    
    words = text.split()
    for i in range(0, len(words), chunk_size - overlap):
        chunk = " ".join(words[i:i + chunk_size])
        chunks.append(chunk)
    return chunks
