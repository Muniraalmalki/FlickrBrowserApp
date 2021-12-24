package com.example.flickrbrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var  recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var etSearch:EditText
    private lateinit var ivImage: ImageView
    private lateinit var searchButton:Button
    private lateinit var linearLayout: LinearLayout
    private lateinit var imageList: ArrayList<ImageData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageList = arrayListOf()
        etSearch = findViewById(R.id.etSearch)
        ivImage = findViewById(R.id.ivImage)
        searchButton = findViewById(R.id.searchButton)
        recyclerView = findViewById(R.id.recyclerView)
        linearLayout = findViewById(R.id.linearLayout)

        recyclerViewAdapter = RecyclerViewAdapter(this,imageList)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager= GridLayoutManager(this,2)

        searchButton.setOnClickListener {
            if(etSearch.text.isNotEmpty()){
                requestAPI()
            }else{

                Toast.makeText(this, "Search field is empty", Toast.LENGTH_LONG).show()
            }
        }
        ivImage.setOnClickListener {
            closeImg()
        }
    }

    private fun requestAPI(){
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { getPhotos() }.await()
            if(data.isNotEmpty()){
                println(data)
                showPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotos(): String{
        var response = ""
        try{
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=54c06333154efb06c9f5673aef734ec6&tags=${etSearch.text}&per_page=&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
            Log.d("TAG", "getPhotos: ${response}")
        }catch(e: Exception){
            println("ISSUE: $e")
        }
        return response
    }


    private suspend fun showPhotos(data: String){
        withContext(Dispatchers.Main){
            val jsonObj = JSONObject(data)
            Log.d("TAG1234", "showPhotos: "+data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")
            println(photos.getJSONObject(0))
            println(photos.getJSONObject(0).getString("farm"))
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                imageList.add(ImageData(title, photoLink))
            }
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }

    fun viewImage(link: String){
        Glide.with(this).load(link).into(ivImage)
        ivImage.isVisible = true
        recyclerView.isVisible = false
        linearLayout.isVisible = false
    }

    private fun closeImg(){
        ivImage.isVisible = false
        recyclerView.isVisible = true
        linearLayout.isVisible = true
    }
}