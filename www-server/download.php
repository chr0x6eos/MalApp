<?php

//Provide the file for download
function download($path)
{
    	// Process download
	if(file_exists($path))
	{
		header('Content-Description: File Transfer');
        	header('Content-Type: application/octet-stream');
        	header('Content-Disposition: attachment; filename="'.basename($path).'"');
        	header('Expires: 0');
        	header('Cache-Control: must-revalidate');
        	header('Pragma: public');
        	header('Content-Length: ' . filesize($path));
        	flush(); // Flush system output buffer
        	readfile($path);
        	exit;
    	}
}

if(isset($_REQUEST["file"])) //If special file is defined, download this file
{
	// Get filename in the parameters
	$file = $_REQUEST["file"];//urldecode($_REQUEST["file"]); // Decode URL-encoded string
    	//Files are stored in data/<filename>.apk
	$filepath = "data/" . $file;
	download($filepath);
}
else //Download default file
{
	//Use update.apk as default if nothing is defined
	$file = 'update-1.0.apk';
 	//Files are stored in data/<filename>.apk
	$filepath = "data/" . $file;
	download($filepath);
}
?>