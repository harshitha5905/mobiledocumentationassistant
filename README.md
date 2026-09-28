# Mobile Document Assistant (RAG-based)

This project consists of an Android application (Kotlin) and a Python backend (FastAPI) that allows users to upload PDF documents and ask questions about them using Retrieval-Augmented Generation (RAG).

## Project Flow
1. **PDF Upload**: The Android app sends a PDF to the Python server.
2. **Indexing**: The server extracts text, chunks it, generates embeddings using `sentence-transformers`, and stores them in a `FAISS` vector database.
3. **Questioning**: The user asks a question via the Android app.
4. **Retrieval**: The server converts the question to an embedding and finds relevant text chunks in the vector database.
5. **Generation**: A response is generated based on the retrieved context and sent back to the app.

## Python Backend Setup

### 1. Prerequisites
- Install Python 3.8 or higher from [python.org](https://www.python.org/).

### 2. Install Requirements
Navigate to the `python_backend` folder and run:
```bash
pip install -r requirements.txt
```

### 3. Start FastAPI Server
Run the following command in the `python_backend` folder:
```bash
python main.py
```
Or use uvicorn directly:
```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

### 4. Verify Server
- Check health: Open `http://localhost:8000/health` in your browser.
- API Docs: Open `http://localhost:8000/docs` to see the interactive documentation.

## Android App Setup

### 1. Emulator Connection
The Android app is configured to connect to `10.0.2.2:8000`. This is the special IP address that allows the Android Emulator to access the `localhost` of your computer.

### 2. Permissions
Ensure Internet permission is granted in the manifest (already added).

### 3. Usage
1. Open the app.
2. Click **Select PDF** to pick a document.
3. Click **Upload PDF** to send it to the backend.
4. Type a question in the input field.
5. Click **Ask Question** to get an answer based on the document.

## Troubleshooting
- **Connection Error**: Ensure the Python server is running and your firewall isn't blocking port 8000.
- **Empty Answer**: Ensure the PDF contains searchable text (not just images).
- **Indexing Error**: Ensure all dependencies in `requirements.txt` are installed correctly.
