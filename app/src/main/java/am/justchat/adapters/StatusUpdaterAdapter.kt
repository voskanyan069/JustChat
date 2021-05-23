package am.justchat.adapters

import am.justchat.api.repos.StatusRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Status
import android.util.Log
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatusUpdaterAdapter {
    companion object {
        fun updateStatus(status: String) {
            StatusRepo.getInstance().statusService!!.updateStatus(
                    Status(
                            login = CurrentUser.login.toString(),
                            newStatus = status
                    )
            ).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.d("mTag", "Status updated - $status")
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mTag", "Fetch error", t)
                }
            })
        }
    }
}