const express = require('express');
const multer = require('multer');
const fs = require('fs');
const axios = require('axios');
const cors = require('cors');

const app = express();
const upload = multer({ dest: '/tmp' });

// Enable CORS for http://localhost:4200
app.use(cors({
  origin: 'http://localhost:4200',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

app.post('/upload', upload.single('file'), async (req, res) => {
  try {
    const stream = fs.createReadStream(req.file.path);
    console.log('Forwarding file to Java service:', req.file);
    const response = await axios.post(
      'http://java-service:8080/process',
      stream,
      { headers: { 'Content-Type': 'text/plain' } }
    );

    res.json(response.data);
  } catch (e) {
    res.status(500).json({ error: e.message });
  } finally {
    fs.unlinkSync(req.file.path);
  }
});

app.listen(3000, () => console.log('Node Gateway on 3000'));
