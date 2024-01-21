package com.example.loginform.data.productsrepository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.loginform.Model.Customer
import com.example.loginform.Model.ItemType
import com.example.loginform.Model.ProductItem
import com.example.loginform.Model.UserRating
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ProductsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storageRef: StorageReference,
    @ApplicationContext private val context: Context,
    private val prefRepository: PrefRepository
) : ProductRepository {
    val contentResolver = context.contentResolver

    private val productsRef = firestore.collection("Products")
    private val coffeeItems = arrayListOf<ProductItem>()
    var cakesItems = arrayListOf<ProductItem>()

    override var selectedProduct: ProductItem? = null

    /*init {   uploadRandomCoffees()
           uploadRandomCakes()
    }*/

    override suspend fun getCoffeeItems(): Resource<List<ProductItem>> {
        return try {

            val resultSnapshot = productsRef.whereEqualTo("productType", "COFFEE").get().await()
            val result = resultSnapshot.toObjects(ProductItem::class.java)
            coffeeItems.clear()
            coffeeItems.addAll(result)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun getCakeItems(): Resource<List<ProductItem>> {
        return try {
            val resultSnapshot = productsRef.whereEqualTo("productType", "CAKE").get().await()
            val result = resultSnapshot.toObjects(ProductItem::class.java)
            if (result.isNotEmpty()) {
                cakesItems.clear()
                cakesItems.addAll(result)
            }
            Resource.Success(result)

        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun getProduct(prodId: String): ProductItem? {
        return try {
            productsRef.document(prodId).get(Source.CACHE).await().toObject(ProductItem::class.java)
        } catch (e: Exception) {
            try {
                productsRef.document(prodId).get().await().toObject(ProductItem::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun rateProduct(key: String, value: Float) {
        try {
            val user: Customer = prefRepository.currentUser.stringToCustomer()!!
            val userRating = UserRating(user.cusId, user.cusFullName, user.cusEmail, value)
            val map = mapOf(
                "prodRating" to FieldValue.increment(value.toDouble()),
                "prodNoOfPeopleRated" to FieldValue.increment(1),
                "ratings" to FieldValue.arrayUnion(userRating)
            )
            productsRef.document(key).set(map, SetOptions.merge())
        } catch (e: Exception) {

        }
    }

    override suspend fun searchProducts(query: String): Resource<List<ProductItem>> {
        return try {
            val arraylist = if (coffeeItems.isNotEmpty() && cakesItems.isNotEmpty()) {
                coffeeItems + cakesItems
            } else {
                productsRef.get().await().toObjects<ProductItem>()
            }

            val filteredList = arraylist.filter {
                it.prodName.contains(query, true)
            }
            Resource.Success(filteredList)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun deleteProduct(it: ProductItem): Resource<Boolean> {
        return try {
            productsRef.document(it.prodId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override suspend fun addProduct(productItem: ProductItem): Resource<Boolean> {
        val mId = productsRef.document().id
        if (productItem.prodId == "") productItem.prodId = mId

        if (productItem.imageUri != null) {
            productItem.prodImage = uploadImage(productItem.imageUri, productItem.prodId)
        }
        return try {
            productsRef.document(productItem.prodId).set(productItem).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    private suspend fun uploadImage(imageUri: Uri?, serviceId: String): String {
        try {
            if (imageUri == null) return ""
            Log.d("cvrr", "Uploading images")
            var downloadUrl = ""
            val imagesRef = storageRef.child(serviceId)
            val completedJob = CoroutineScope(Dispatchers.IO).async {
                val bytes = getCompressedImage(contentResolver, imageUri)
                val putTask =
                    imagesRef.child(System.currentTimeMillis().toString()).putBytes(bytes).await()
                downloadUrl = putTask.storage.downloadUrl.await().toString()
            }
            completedJob.await()
            Log.d("cvrr", "Uploading images completed after await")
            return downloadUrl
        } catch (e: Exception) {
            Log.d("cvrr", "error loading Exc $e")
            return ""
        }


    }

    private fun getCompressedImage(
        contentResolver: ContentResolver, imageUri: Uri?
    ): ByteArray {

        var bmp: Bitmap? = null
        try {
            bmp = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val baos = ByteArrayOutputStream()

        bmp?.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        return baos.toByteArray()
    }

    fun uploadRandomCoffees() {
        val list = arrayListOf<ProductItem>()
        list.apply {
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Latte",
                    prodDes = "Probably the most popular coffee drink on a café menu, the café latte is usually made with 1 part espresso",
                    prodPrice = 2.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c6/Latte_art_3.jpg/330px-Latte_art_3.jpg",
                    productType = ItemType.COFFEE
                )
            )

            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Mocha",
                    prodDes = "The sweetest type of coffee to get and most commonly available is a mocha,",
                    prodPrice = 2.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c6/Latte_art_3.jpg/330px-Latte_art_3.jpg",
                    productType = ItemType.COFFEE
                )
            )
            add(ProductItem(productsRef.document().id, "Mocha"))
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Cappuccino",
                    prodDes = "Cappuccino is made with one part espresso, one part steamed milk and one part milk foam. ",
                    prodPrice = 4.99,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Macchiato",
                    prodDes = "A macchiato is made with a shot of espresso topped with a small amount of milk or milk foam. ",
                    prodPrice = 6.99,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Americano",
                    prodDes = "With a similar flavor to black coffee, the americano consists of an espresso shot diluted in hot water.",
                    prodPrice = 6.59,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Flat white",
                    prodDes = "The heated debates between Australia and New Zealand to stake the claims of Flat White",
                    prodPrice = 4.99,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Iced coffee",
                    prodDes = "Unlike cold brew, iced coffee is chilled hot coffee.",
                    prodPrice = 3.99,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Affogato",
                    prodDes = "Not quite a drink, the affogato is a decadent, easy-to-make dessert",
                    prodPrice = 7.99,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Café au lait",
                    prodDes = "Cafe au lait: “Coffee with milk” is a combination of strong coffee and steamed milk in a 1:1 ratio.",
                    prodPrice = 7.99,
                    prodImage = "https://cdn.apartmenttherapy.info/image/upload/f_auto,q_auto:eco,c_fill,g_center,w_730,h_913/k%2FPhoto%2FRecipe%20Ramp%20Up%2F2022-07-Cappuccino%2FCappuccino",
                    productType = ItemType.COFFEE
                )
            )

        }
        list.forEach {
            productsRef.document(it.prodId).set(it)
        }
    }

    fun uploadRandomCakes() {
        val list = arrayListOf<ProductItem>()
        list.apply {
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Fat rascal",
                    prodDes = "A pastry made from dried fruit, candied peel, and oats.",
                    prodPrice = 2.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Fat_Rascal_cookies_%28cropped%29.jpg/180px-Fat_Rascal_cookies_%28cropped%29.jpg",
                    productType = ItemType.CAKE
                )
            )

            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Faworki",
                    prodDes = "A sweet crisp cake in the shape of a bow.",
                    prodPrice = 2.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Faworki_%28plate%29.jpg/180px-Faworki_%28plate%29.jpg",
                    productType = ItemType.CAKE
                )
            )
            add(ProductItem(productsRef.document().id, "Mocha"))
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Fig cake",
                    prodDes = "A cake prepared with fig as a primary ingredient.",
                    prodPrice = 4.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Fig_Skillet_Cake_%2814430102033%29.jpg/180px-Fig_Skillet_Cake_%2814430102033%29.jpg",
                    productType = ItemType.CAKE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Financier",
                    prodDes = "A small molded almond flour and beurre noisette cake.",
                    prodPrice = 6.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Two_rectangular_financiers.jpg/180px-Two_rectangular_financiers.jpg",
                    productType = ItemType.CAKE
                )
            )

            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Flan cake",
                    prodDes = "A chiffon or sponge cake baked with a layer of leche flan (crème caramel) on top and drizzled with caramel syrup.",
                    prodPrice = 4.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Leche_flan_cake_%28Philippines%29_3.jpg/180px-Leche_flan_cake_%28Philippines%29_3.jpg",
                    productType = ItemType.CAKE
                )
            )

            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Flourless chocolate cake",
                    prodDes = "A dense, gluten-free cake prepared with chocolate.",
                    prodPrice = 7.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Flourless_Chocolate_Cake_with_Bourbon_Vanilla_Ice_Cream.jpg/180px-Flourless_Chocolate_Cake_with_Bourbon_Vanilla_Ice_Cream.jpg",
                    productType = ItemType.CAKE
                )
            )
            add(
                ProductItem(
                    prodId = productsRef.document().id,
                    prodName = "Fondant Fancy",
                    prodDes = "A small sponge cake topped with fondant icing.",
                    prodPrice = 7.99,
                    prodImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Mr_Kipling_french_fancy_%2814142526369%29.jpg/180px-Mr_Kipling_french_fancy_%2814142526369%29.jpg",
                    productType = ItemType.CAKE
                )
            )
        }

        list.forEach {
            productsRef.document(it.prodId).set(it)
        }
    }


}