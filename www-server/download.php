<?php
if(isset($_REQUEST["file"])){
	// Get parameters
	//echo 'request set';
	$file = $_REQUEST["file"];//urldecode($_REQUEST["file"]); // Decode URL-encoded string
    	$filepath = "data/" . $file;
    
    	// Process download
	if(file_exists($filepath)){
		//echo 'file exists!';
        	header('Content-Description: File Transfer');
        	header('Content-Type: application/octet-stream');
        	header('Content-Disposition: attachment; filename="'.basename($filepath).'"');
        	header('Expires: 0');
        	header('Cache-Control: must-revalidate');
        	header('Pragma: public');
        	header('Content-Length: ' . filesize($filepath));
        	flush(); // Flush system output buffer
        	readfile($filepath);
        	exit;
    	}
}
?>