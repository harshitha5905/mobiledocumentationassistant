import faiss
import numpy as np

class VectorStore:
    def __init__(self, dimension=384):
        # all-MiniLM-L6-v2 outputs 384 dimensional vectors
        self.index = faiss.IndexFlatL2(dimension)
        self.documents = []

    def clear(self):
        """Clears the vector database."""
        self.index.reset()
        self.documents = []

    def add_documents(self, chunks, embeddings):
        """Adds text chunks and their embeddings to FAISS."""
        if not chunks:
            return
        self.documents.extend(chunks)
        embeddings_np = np.array(embeddings).astype('float32')
        self.index.add(embeddings_np)

    def search(self, query_embedding, k=3):
        """Retrieves top k closest matching document chunks."""
        if not self.documents:
            return []
        
        query_np = np.array([query_embedding]).astype('float32')
        distances, indices = self.index.search(query_np, k)
        
        results = []
        for idx in indices[0]:
            if idx != -1 and idx < len(self.documents):
                results.append(self.documents[idx])
        return results

# Singleton instance of the vector store
vector_store_instance = VectorStore()
