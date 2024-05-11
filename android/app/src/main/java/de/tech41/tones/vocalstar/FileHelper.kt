package de.tech41.tones.vocalstar

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.InputStream


class FileHelper(context: Context, viewModel : Model) {

    var context : Context = context
    var viewModel : Model = viewModel

    companion object {
         val LAST_URL = "LAST_URL"
    }

    private val SHARED_PREFS = "sharedPrefs"

    fun saveData( key:String, value:String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun loadData(key:String): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

    fun isData(key:String): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        return sharedPreferences.contains(key)
    }

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

        if (isData(LAST_URL)) {
            val file = File(Uri.parse(loadData(LAST_URL)).path)
            if (file.exists()) {
                viewModel.playerUri = Uri.parse(loadData(LAST_URL))
                return
            }
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
                saveData(LAST_URL,"file://" + slowPath)
            }else{
                if (!isData(LAST_URL)) {
                    saveData(LAST_URL, "file://" + slowPath)
                }
                viewModel.playerUri = Uri.parse(loadData(LAST_URL))
            }
        }else {
            Log.e(TAG, "folder creation failed " + sd_main)
        }
    }

}