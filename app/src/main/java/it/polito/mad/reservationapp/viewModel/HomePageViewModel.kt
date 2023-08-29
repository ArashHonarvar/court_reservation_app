package it.polito.mad.reservationapp.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.MainThread
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.reservationapp.model.firebase.CardHighlights
import it.polito.mad.reservationapp.model.firebase.service.FirebaseServiceImpl
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class HomePageViewModel(application: Application) : AndroidViewModel(application) {
    private val _firebaseRepository = FirebaseServiceImpl()
    private val _coverImage = MutableLiveData<ImageBitmap>()
    private val _cardImages = MutableLiveData<List<CardHighlights>>()
    val coverImage = _coverImage
    val cardImages = _cardImages

    init {
        println("ViewModel created")
        viewModelScope.launch {
            if(application.applicationContext.fileList().contains("cover-image.png")){
                val image = File(application.applicationContext.filesDir, "cover-image.png")
                _coverImage.value = BitmapFactory.decodeFile(image.absolutePath).asImageBitmap()
            }else{
                val data = _firebaseRepository.getCoverImage()
                val image = BitmapFactory.decodeByteArray(data,0,data.size)
                _coverImage.value = image.asImageBitmap()
                saveBitmapToPNGFile(image,"","cover-image.png", application.applicationContext)
            }
        }
        viewModelScope.launch {
            val dir = createDirIfNotExists(application.applicationContext, "highlights-card")
            val cardNames = _firebaseRepository.getHighlightsCardName()

            val cachedCards = dir.listFiles()

            if(cachedCards?.map { it.name }?.containsAll(cardNames) == true){
                _cardImages.value = cachedCards.map {
                    CardHighlights(it.name, BitmapFactory.decodeFile(it.absolutePath).asImageBitmap() )
                }
            }
            else{
                val cards =  _firebaseRepository.getCardImages().map {
                    CardHighlights(it.title, it.bitmap)
                }
                cards.forEach {
                    saveBitmapToPNGFile(it.bitmap.asAndroidBitmap(),"highlights-card",it.title,application.applicationContext)
                }
                _cardImages.value = cards
            }
        }
    }

    private fun saveBitmapToPNGFile(bitmap: Bitmap,dirname: String, filename: String, context: Context) {
        val dir = File(context.filesDir, dirname)
        val fileOutputStream = FileOutputStream(File(dir, filename))
//        val fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    private fun createDirIfNotExists(context: Context, dirname: String): File{
        val dir = File(context.filesDir, dirname)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }
}
