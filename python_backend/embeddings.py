from sentence_transformers import SentenceTransformer

# Load a pre-trained model for embeddings
model = SentenceTransformer('all-MiniLM-L6-v2')

def generate_embeddings(texts):
    """Converts a list of texts into embeddings."""
    return model.encode(texts)

def generate_query_embedding(query):
    """Converts a single query into an embedding."""
    return model.encode([query])[0]
