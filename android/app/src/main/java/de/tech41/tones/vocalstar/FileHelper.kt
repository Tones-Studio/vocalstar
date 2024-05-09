package de.tech41.tones.vocalstar

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class FileHelper(context: Context, viewModel : Model) {

    var context : Context = context
    var viewModel : Model = viewModel

    val TAG = "de.tech41.tones.vocalstar.FileHelper"

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
    fun makeAppFolder(isExternal: Boolean){
        var path = ""
        if(isExternal){
            path = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.path + "/vocalstar"
        }else{
            path = context.getFilesDir().path + "/vocalstar"
        }

        var sd_main = File(path)
        var success = false
        if (!sd_main.exists()) {
            success = sd_main.mkdirs()
        }else {
            success = true
        }

        // Folder exists
        if(success) {
            // check if slow.mp3 exists
            var slowPath :String = path + "/slow.mp3"
            val file = File(slowPath)
            if (!file.exists()) {
                // copy slow.mp3
                Log.d(TAG, "slow.mp3 copied to " + slowPath)
                var instream = context.getResources().openRawResource(R.raw.slow)
                file.copyInputStreamToFile(instream)
                viewModel.playerUri = Uri.parse("file://" + slowPath)
            }else{
                Log.d(TAG, "slow.mp3 did exist at  " + slowPath)
                viewModel.playerUri = Uri.parse("file://" + slowPath)
            }
        }else {
            Log.e(TAG, "folder creation failed " + sd_main)
        }
    }
}