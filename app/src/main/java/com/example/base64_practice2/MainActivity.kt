package com.example.base64_practice2


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.system.Os.close
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.example.base64_practice2.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    var sImage:String=""

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        //encode押した処理
        binding.encode.setOnClickListener {
            //パーミッションの許可
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                //許可が下りない時
                //再度許可を求める
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100
                )
            } else {
                //許可が下りた時
                //関数を作成
                selectImage()
            }

        }
        //decode押した処理
        binding.decode.setOnClickListener {

            val bytes : ByteArray = Base64.getDecoder().decode(sImage.toByteArray())
            val bitmap:Bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            binding.imageView.setImageBitmap(bitmap)

        }
    }
    val REQUEST_GALLERY = 0

    private fun selectImage() {

        // 以前のデータをクリアする
        binding.textView.setText("")
        binding.imageView.setImageBitmap(null)
//インテントの初期化（インテントとは、他のアクティビティやアプリケーションなどと情報のやり取りを行うための箱のようなもの）
        val intent = Intent(Intent.ACTION_VIEW)
        //タイプ
        intent.setType("image/jpeg")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        //結果
        startActivityForResult(intent, REQUEST_GALLERY)

    }






    //選択した画像をImageViewに貼り付け(アルバムが閉じたとき)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                val inn: InputStream? = data?.getData().let { data?.getData()
                    ?.let { it1 -> contentResolver.openInputStream(it1) } }

                val img: Bitmap = BitmapFactory.decodeStream(inn)

              //  binding.imageView.setImageBitmap(img)

                val stream = ByteArrayOutputStream()

                img.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                val byte: ByteArray = stream.toByteArray()

               sImage =Base64.getEncoder().encodeToString(byte)

                binding.textView.setText(sImage)
                inn?.close()

            } catch (e: Exception) {

            }
        }
    }

    //パーミッションの状況
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //許可が下りた時
            //関数呼び出し
            selectImage()
        } else {
            Toast.makeText(getApplicationContext(), "アルバムへのアクセスを拒否", Toast.LENGTH_SHORT).show()
        }
    }


}

