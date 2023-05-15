package com.example.taxibooking.data.repository


import android.content.ContentValues
import android.util.Log
import com.example.taxibooking.domain.model.TaxiRequest
import com.example.taxibooking.domain.model.User
import com.example.taxibooking.domain.model.UserType
import com.example.taxibooking.presentation.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.location.Location
import com.google.firebase.firestore.ktx.getField


class TaxiBookingRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) {

    private var lastRequestID = ""
    fun saveUserToFirebase(user: User) {

        val userMap = hashMapOf(
            "id" to user.id,
            "email" to user.email,
            "firstName" to user.firstName,
            "secondName" to user.secondName,
            "type" to user.type.name,
            "status" to user.status,
            "latitude" to user.latitude,
            "longitude" to user.longitude,
            "phoneNumber" to user.phoneNumber,
            "travelStatus" to user.travelStatus,
            "seats" to user.seats,
        )
        val path = user.type.name
        db.collection(path).document(user.phoneNumber).set(userMap)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
            }
    }

    suspend fun updateUserInfo(user: User) {
        val database = db.collection(user.type.name).get().await()
        if (user.type == UserType.DRIVER) {
            for (document in database) {
                if (document.data.getValue("id") == user.id) {
                    db.collection(user.type.name).document(document.id)
                        .set(user)
                }
            }
        } else {
            for (document in database) {
                if (document.data.getValue("id") == user.id) {
                    db.collection(user.type.name).document(document.id)
                        .set(user)
                }
            }
        }
    }

    fun listenDriverCustomerLocationChange(driverID: String, locationCallback: (Pair<Location, Location>) -> Unit){
        var locations: Pair<Location, Location>? = null
        val tasksCollection = db.collection("DriverCustomerRelation")
        tasksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                print(exception.localizedMessage)
            } else {

                snapshot?.documents?.forEach { documentSnapshot ->
                    documentSnapshot?.let { documentSnapshotInner ->
                        if (documentSnapshotInner.id.contains(driverID, false)) {
                            if (documentSnapshotInner.data?.isNotEmpty() == true) {
                                val driverData = documentSnapshotInner.get("driver") as HashMap<String, Any>
                                val customerData = documentSnapshotInner.get("customer") as HashMap<String, Any>

                                val userTypeString = driverData["type"] as String
                                val driver = User(
                                    id = driverData["id"] as String,
                                    firstName = driverData["firstName"] as String,
                                    secondName = driverData["secondName"] as String,
                                    email = driverData["email"] as String,
                                    type = UserType.valueOf(userTypeString),
                                    status = driverData["status"] as Boolean,
                                    latitude = driverData["latitude"] as Double,
                                    longitude = driverData["longitude"] as Double,
                                    phoneNumber = driverData["phoneNumber"] as String,
                                    travelStatus = driverData["travelStatus"] as Boolean? ?: false,
                                    seats = driverData["seats"] as Long
                                )

                                val userTypeString2 = customerData["type"] as String
                                val customer = User(
                                    id = customerData["id"] as String,
                                    firstName = customerData["firstName"] as String,
                                    secondName = customerData["secondName"] as String,
                                    email = customerData["email"] as String,
                                    type = UserType.valueOf(userTypeString2),
                                    status = customerData["status"] as Boolean,
                                    latitude = customerData["latitude"] as Double,
                                    longitude = customerData["longitude"] as Double,
                                    phoneNumber = customerData["phoneNumber"] as String,
                                    travelStatus = customerData["travelStatus"] as Boolean? ?: false,
                                    seats = customerData["seats"] as Long
                                )

                                val customerLocation = Location("provider")
                                customerLocation.latitude = customer.latitude
                                customerLocation.longitude = customer.longitude

                                val driverLocation = Location("provider")
                                driverLocation.latitude = driver.latitude
                                driverLocation.longitude = driver.longitude
                                locations = Pair(customerLocation, driverLocation)
                                locations?.let {
                                    locationCallback.invoke(it.copy())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getActiveUsers(userType: UserType, switchStateFlow: Flow<Boolean>): Flow<List<User>> = callbackFlow {

        var listenerRegistration: ListenerRegistration? = null

        switchStateFlow.collect { switchState ->
            if (switchState) {
                val tasksCollection = db.collection(userType.name)
                listenerRegistration = tasksCollection.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        close(exception)
                    } else {
                        val list = arrayListOf<User>()
                        snapshot?.documents?.forEach { documentSnapshot ->
                            documentSnapshot?.let {
                                if (it.data?.isNotEmpty() == true) {
                                    val userTypeString = it.getString("type") as String
                                    val user = User(
                                        id = it.getString("id") as String,
                                        firstName = it.getString("firstName") as String,
                                        secondName = it.getString("secondName") as String,
                                        email = it.getString("email") as String,
                                        type = UserType.valueOf(userTypeString),
                                        status = it.getBoolean("status") as Boolean,
                                        latitude = it.getDouble("latitude") as Double,
                                        longitude = it.getDouble("longitude") as Double,
                                        phoneNumber = it.getString("phoneNumber") as String,
                                        travelStatus = it.getBoolean("travelStatus") ?: false,
                                        seats = it.getField<Long>("seats") ?: 0L
                                    )
                                    list.add(user)
                                }

                            }
                        }
                        trySend(list).isSuccess
                    }
                }
            } else {
                listenerRegistration?.remove()
                listenerRegistration = null
            }
        }

        awaitClose { listenerRegistration?.remove() }
    }

    suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
        val driverCollection = db.collection(UserType.DRIVER.name).document(phoneNumber).get().await()
        val customerCollection = db.collection(UserType.CUSTOMER.name).document(phoneNumber).get().await()

        return if (driverCollection.data != null) {
            val driver = driverCollection.data!!
            val userTypeString = driver["type"] as String
            val user = User(
                id = driver["id"] as String,
                firstName = driver["firstName"] as String,
                secondName = driver["secondName"] as String,
                email = driver["email"] as String,
                type = UserType.valueOf(userTypeString),
                status = driver["status"] as Boolean,
                latitude = driver["latitude"] as Double,
                longitude = driver["longitude"] as Double,
                phoneNumber = driver["phoneNumber"] as String,
                travelStatus = driver["travelStatus"] as Boolean,
                seats = driver["seats"] as Long
            )
            user
        } else {
            val customer = customerCollection.data
            if (customer != null) {
                val userTypeString = customer["type"] as String
                val user = User(
                    id = customer["id"] as String,
                    firstName = customer["firstName"] as String,
                    secondName = customer["secondName"] as String,
                    email = customer["email"] as String,
                    type = UserType.valueOf(userTypeString),
                    status = customer["status"] as Boolean,
                    latitude = customer["latitude"] as Double,
                    longitude = customer["longitude"] as Double,
                    phoneNumber = customer["phoneNumber"] as String,
                    travelStatus = customer["travelStatus"] as Boolean,
                    seats = customer["seats"] as Long,
                )
                user
            } else {
                null
            }

        }
    }

    fun createTaxiRequest(driverId: String, customerId: String) {
        val taxiRequest = hashMapOf(
            "customerId" to customerId,
            "driverId" to driverId,
            "status" to "pending",
            "timestamp" to FieldValue.serverTimestamp(),
        )

        val db = FirebaseFirestore.getInstance()
        val path = "${Constants.getCurrentUser().id} - $driverId"
        db.collection("taxiRequests").document(path)
            .set(taxiRequest)
            .addOnSuccessListener { documentReference ->
            }
            .addOnFailureListener { e ->

            }
    }

    suspend fun createCustomerDriverRelation(customerID: String, driver: User) {
        val db = db.collection("DriverCustomerRelation").document("$customerID - ${driver.id}")
        val customer = getCustomerByID(customerID)

        customer?.let {
            val relationData = hashMapOf(
                "customer" to customer,
                "driver" to driver
            )
            updateUserInfo(customer.copy(travelStatus = true))
            updateUserInfo(driver.copy(travelStatus = true))
            db.set(relationData)
        }

    }


    private suspend fun getCustomerByID(customerId: String): User? {
        val customerCollection = db.collection(UserType.CUSTOMER.name).get().await()
        for (document in customerCollection.documents) {
            if (document.data?.getValue("id") == customerId) {
                val userTypeString = document["type"] as String
                return User(
                    id = document["id"] as String,
                    firstName = document["firstName"] as String,
                    secondName = document["secondName"] as String,
                    email = document["email"] as String,
                    type = UserType.valueOf(userTypeString),
                    status = document["status"] as Boolean,
                    latitude = document["latitude"] as Double,
                    longitude = document["longitude"] as Double,
                    phoneNumber = document["phoneNumber"] as String,
                    travelStatus = document["travelStatus"] as Boolean,
                    seats = document["seats"] as Long,
                )
            }
        }
        return null
    }

    suspend fun getDriverByID(driverID: String): User? {
        val customerCollection = db.collection(UserType.DRIVER.name).get().await()
        for (document in customerCollection.documents) {
            if (document.data?.getValue("id") == driverID) {
                val userTypeString = document["type"] as String
                return User(
                    id = document["id"] as String,
                    firstName = document["firstName"] as String,
                    secondName = document["secondName"] as String,
                    email = document["email"] as String,
                    type = UserType.valueOf(userTypeString),
                    status = document["status"] as Boolean,
                    latitude = document["latitude"] as Double,
                    longitude = document["longitude"] as Double,
                    phoneNumber = document["phoneNumber"] as String,
                    travelStatus = document["travelStatus"] as Boolean,
                    seats = document["seats"] as Long,
                )
            }
        }
        return null
    }


    fun listenForTaxiRequests(driverId: String, taxiRequest: (TaxiRequest) -> Unit) {
        db.collection("taxiRequests").addSnapshotListener { value, error ->
            value?.let {
                for (document in value.documents) {
                    if (document.id.contains(driverId)) {
                        val customerID = document.getString("customerId")
                        val driverID = document.getString("driverId")
                        val status = document.getString("status")
                        val timeStamp = document.getTimestamp("timestamp")
                        if (customerID != null && driverID != null && status != null && timeStamp != null) {
                            taxiRequest.invoke(TaxiRequest(customerID, driverID, status, timeStamp, document.id))
                            break
                        }
                    }
                }
            }

        }


    }

    fun updateTaxiRequestStatus(requestId: String, status: String) {
        db.collection("taxiRequests")
            .document(requestId)
            .update("status", status)
            .addOnSuccessListener {
                Log.d("updateTaxiRequestStatus", "Taxi request status updated.")
            }
            .addOnFailureListener { e ->
                Log.w("updateTaxiRequestStatus", "Error updating taxi request status.", e)
            }
    }

    fun deleteDriverCustomerRelationAndRequest(documentID: String) {
        Log.e("DocumentID-deleteDriverCustomerRelationAndRequest", documentID)
        if (lastRequestID == documentID) return
        else lastRequestID = documentID
        val taxiRequestRef = db.collection("taxiRequests").document(documentID)
        val relationRef = db.collection("DriverCustomerRelation").document(documentID)

        taxiRequestRef.delete()
            .addOnSuccessListener {
                // Document successfully deleted
            }
            .addOnFailureListener { e ->
                // Handle any errors here
            }
        relationRef.delete()
            .addOnSuccessListener {
                // Document successfully deleted
            }
            .addOnFailureListener { e ->
                // Handle any errors here
            }
    }


}