def generate_answer(query, retrieved_chunks):
    """
    Generates an answer based strictly on the retrieved document chunks.
    As this is a lightweight frontend-backend prototype suitable for college project reviews,
    we implement a rules-based context extraction and professional text synthesizer.
    """
    if not retrieved_chunks:
        return "I could not find any relevant information in the uploaded PDF to answer your question."

    # Combine context
    context = " ".join(retrieved_chunks)
    
    # Simple lightweight matching engine to simulate exact retrieval behavior
    # For a real LLM endpoint, a call to OpenAI/HuggingFace can be added here.
    query_lower = query.lower()
    keywords = [w for w in query_lower.split() if len(w) > 3]
    
    sentences = context.split('. ')
    matching_sentences = []
    
    for sentence in sentences:
        if any(kw in sentence.lower() for kw in keywords):
            matching_sentences.append(sentence.strip())
            
    if matching_sentences:
        answer = "Based on the document: " + ". ".join(matching_sentences[:3]) + "."
    else:
        answer = f"The document mentions information related to the topic, specifically: {retrieved_chunks[0][:200]}..."
        
    return answer
