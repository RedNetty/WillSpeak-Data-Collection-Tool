# Voice Recorder for AI Dataset Collection

This application loads phrases from a JSON file and allows users to record themselves saying these phrases. The recordings are saved as 16kHz WAV files, which is a common format for AI training data. The application is specifically designed to collect voice data from people with various speech patterns including those with speech impairments.

## Features

- Loads phrases from a JSON file
- Records audio at 16kHz sample rate (mono, 16-bit)
- Displays a real-time audio level meter
- Collects user profile information including speech impairment data
- Saves recordings with unique filenames in individual directories
- Maintains detailed metadata for each recording
- Provides simple navigation between phrases
- User-friendly interface with modern design

## Requirements

- Java 8 or higher
- Google Gson library for JSON processing

## Installation

1. **Install Java** if you don't have it already
2. **Build with Maven**:
   ```
   mvn clean package
   ```
   This will create an executable JAR file in the `target` directory.

3. **Run the application**:
   ```
   java -jar target/WillSpeak-Data-Collection-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

4. **Provide phrases.json file**:
   - Place the `phrases.json` file in the same directory as the JAR file
   - Format: array of objects with `id`, `text`, and `category` properties
   - See the example provided in the repository

## Data Storage Structure

The application stores recordings in a directory structure designed for AI dataset organization:

```
recordings/
├── recordings_index.json (global index of all recordings)
├── <recording-id1>/
│   ├── metadata.json (individual recording metadata)
│   └── recording.wav (audio file)
├── <recording-id2>/
│   ├── metadata.json
│   └── recording.wav
└── ...
```

Each recording has:
1. Its own directory named with a unique ID
2. A WAV file containing the audio
3. A metadata.json file with details about:
   - The phrase text and ID
   - The user's profile (including speech characteristics)
   - Audio properties (sample rate, etc.)
   - Recording timestamp

## Metadata Details

Each `metadata.json` file contains comprehensive information:

```json
{
  "recordingId": "unique-identifier",
  "userId": "user-identifier",
  "phraseId": "phrase-id-from-phrases-json",
  "phraseText": "The actual text that was recorded",
  "phraseCategory": "category-of-phrase",
  "recordingFile": "filename.wav",
  "timestamp": "YYYYMMDD_HHMMSS",
  "speechType": "clear or impaired",
  "speechImpairmentDetails": "specific impairment details if applicable",
  "deviceInfo": "operating system info",
  "audioProperties": {
    "sampleRate": 16000.0,
    "bitsPerSample": 16,
    "channels": 1
  },
  "additionalInfo": {}
}
```

## Usage

1. Run the application
2. Set up your user profile when prompted (including speech impairment information if applicable)
3. Navigate through phrases using Previous/Next buttons
4. Click Record to start recording yourself saying the displayed phrase
5. Click Stop when finished
6. Recordings will be saved automatically in the directory structure described above

## Customization

- Add more phrases by editing the `phrases.json` file
- Modify the UI appearance by changing the color constants in `UIHelper.java`
- Adjust the output directory structure in `DataManager.java`

## For Researchers

This application is designed to facilitate the collection of speech data for AI training, especially for improving speech recognition for people with speech impairments. Key features for researchers:

- Consistent 16kHz, 16-bit mono WAV format
- Comprehensive metadata for each recording
- Organized directory structure for easy dataset management
- Speech impairment categorization for data analysis
- Demographic information collection