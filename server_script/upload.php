<?php
/**
 * Dhaaga Artisans Platform — Shared Hosting Image Upload API
 * Place this file on your PHP shared hosting server (e.g., https://yourdomain.com/api/upload.php)
 * Make sure to create an 'uploads' directory next to this file and grant write permissions (chmod 755 or 777).
 */

// Debugging & Logging Toggle
define('ENABLE_LOGGING', true);
define('LOG_FILE', __DIR__ . '/debug_upload.log');

function writeDebugLog($message, $level = 'INFO') {
    if (!ENABLE_LOGGING) return;
    $timestamp = date('Y-m-d H:i:s');
    $logLine = "[$timestamp] [$level] " . (is_array($message) || is_object($message) ? json_encode($message) : $message) . PHP_EOL;
    file_put_contents(LOG_FILE, $logLine, FILE_APPEND | LOCK_EX);
}

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, X-API-KEY');

// Handle preflight CORS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    writeDebugLog("CORS OPTIONS preflight request received from " . ($_SERVER['REMOTE_ADDR'] ?? 'unknown'));
    http_response_code(200);
    exit();
}

// Security API Key (Change this secret key in production & match in app)
define('SECRET_API_KEY', 'dhaaga_sih2026_secure_upload_key');

// Check Request Method
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    writeDebugLog("Rejected non-POST method: " . $_SERVER['REQUEST_METHOD'], 'WARN');
    http_response_code(405);
    echo json_encode([
        'status' => 'error',
        'message' => 'Only POST requests are allowed.'
    ]);
    exit();
}

// Verify API Key from Header or POST parameter
$apiKey = $_SERVER['HTTP_X_API_KEY'] ?? $_POST['api_key'] ?? '';
writeDebugLog("Incoming POST upload request from IP: " . ($_SERVER['REMOTE_ADDR'] ?? 'unknown') . " | User-Agent: " . ($_SERVER['HTTP_USER_AGENT'] ?? 'none'));

if ($apiKey !== SECRET_API_KEY) {
    writeDebugLog("Unauthorized access attempt. Provided key: " . substr($apiKey, 0, 5) . "...", 'ERROR');
    http_response_code(401);
    echo json_encode([
        'status' => 'error',
        'message' => 'Unauthorized: Invalid API Key.'
    ]);
    exit();
}

// Check if image file was uploaded
if (!isset($_FILES['image']) || $_FILES['image']['error'] !== UPLOAD_ERR_OK) {
    $errorCode = $_FILES['image']['error'] ?? UPLOAD_ERR_NO_FILE;
    writeDebugLog("Upload failed: No image field or upload error code: " . $errorCode, 'ERROR');
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'No image file received or upload error code: ' . $errorCode
    ]);
    exit();
}

$file = $_FILES['image'];
writeDebugLog("Received file: " . $file['name'] . " | Size: " . $file['size'] . " bytes | Temp: " . $file['tmp_name']);

// Max file size: 10 MB
$maxSize = 10 * 1024 * 1024;
if ($file['size'] > $maxSize) {
    writeDebugLog("File size exceeds 10MB limit: " . $file['size'], 'ERROR');
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'File size exceeds maximum limit of 10MB.'
    ]);
    exit();
}

// Validate MIME type
$allowedTypes = [
    'image/jpeg' => 'jpg',
    'image/jpg'  => 'jpg',
    'image/png'  => 'png',
    'image/webp' => 'webp',
    'image/gif'  => 'gif'
];

$finfo = finfo_open(FILEINFO_MIME_TYPE);
$mimeType = finfo_file($finfo, $file['tmp_name']);
finfo_close($finfo);

writeDebugLog("Detected file MIME type: " . $mimeType);

if (!array_key_exists($mimeType, $allowedTypes)) {
    writeDebugLog("Rejected invalid MIME type: " . $mimeType, 'ERROR');
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid file format. Allowed formats: JPG, PNG, WEBP, GIF.'
    ]);
    exit();
}

// Create uploads directory if it doesn't exist
$uploadDir = __DIR__ . '/uploads/';
if (!is_dir($uploadDir)) {
    if (!mkdir($uploadDir, 0755, true)) {
        writeDebugLog("Failed to create directory: " . $uploadDir, 'CRITICAL');
    }
}

// Generate unique sanitized filename
$extension = $allowedTypes[$mimeType];
$uniqueName = 'dhaaga_' . date('Ymd_His') . '_' . bin2hex(random_bytes(6)) . '.' . $extension;
$targetPath = $uploadDir . $uniqueName;

// Move uploaded file to destination
if (move_uploaded_file($file['tmp_name'], $targetPath)) {
    // Construct public direct URL
    $protocol = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on') ? 'https' : 'http';
    $host = $_SERVER['HTTP_HOST'];
    $dir = dirname($_SERVER['SCRIPT_NAME']);
    $dir = ($dir === '/' || $dir === '\\') ? '' : $dir;
    
    $publicUrl = $protocol . '://' . $host . $dir . '/uploads/' . $uniqueName;

    writeDebugLog("SUCCESS: File saved to " . $targetPath . " | Public URL: " . $publicUrl, 'SUCCESS');

    http_response_code(200);
    echo json_encode([
        'status' => 'success',
        'url' => $publicUrl,
        'filename' => $uniqueName,
        'size' => $file['size'],
        'mime' => $mimeType
    ], JSON_UNESCAPED_SLASHES);
} else {
    writeDebugLog("CRITICAL: move_uploaded_file failed for target path: " . $targetPath, 'CRITICAL');
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to save uploaded file on server. Check folder permissions.'
    ]);
}
