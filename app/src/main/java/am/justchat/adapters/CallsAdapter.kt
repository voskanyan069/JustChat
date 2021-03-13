package am.justchat.adapters

import am.justchat.R
import am.justchat.holders.CallsViewHolder
import am.justchat.models.Call
import am.justchat.states.CallsState
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CallsAdapter(private val dataSet: List<Call>) : RecyclerView.Adapter<CallsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calls_list_item, parent, false)
        return CallsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallsViewHolder, position: Int) {
        holder.bind()

        val item = dataSet[position]
        val callState = when(item.callsState) {
            CallsState.INCOMING -> R.drawable.ic_phone_incoming
            CallsState.OUTGOING -> R.drawable.ic_phone_outgoing
            CallsState.MISSED -> R.drawable.ic_phone_missed
            CallsState.UNANSWERED -> R.drawable.ic_phone_unanswered
        }

        holder.profileUsername.text = item.profileUsername
        holder.callMessage.text = item.callTime
        holder.callStateImage.setImageResource(callState)
        Picasso.get().load(item.profileImage).fit().centerCrop().into(holder.profileImage)
    }

    override fun getItemCount(): Int = dataSet.size
}