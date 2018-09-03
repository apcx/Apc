package apc.android

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import android.widget.TextView
import apc.common.toJson
import java.util.*

class FieldsAdapter(bean: Any) : ListAdapter<Pair<String, CharSequence>, MapHolder>(PairCallback()) {

    init {
        submitList((bean as? Class<*> ?: bean.javaClass).declaredFields.map {
            it.isAccessible = true
            var value = it[bean]
            if (value is Array<*>) value = Arrays.toString(value).run { substring(1, lastIndex) }
            Pair(it.name, value as? CharSequence ?: value.toJson())
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MapHolder(parent.inflate(R.layout.item_property))

    override fun onBindViewHolder(holder: MapHolder, position: Int) {
        val item = getItem(position)
        holder.get<TextView>(R.id.name_view).text = item.first
        holder.get<TextView>(R.id.value_view).text = item.second
    }
}

class PairCallback<Key, Value> : DiffUtil.ItemCallback<Pair<Key, Value>>() {
    override fun areItemsTheSame(oldItem: Pair<Key, Value>, newItem: Pair<Key, Value>) = oldItem.first == newItem.first
    override fun areContentsTheSame(oldItem: Pair<Key, Value>, newItem: Pair<Key, Value>) = oldItem == newItem
}