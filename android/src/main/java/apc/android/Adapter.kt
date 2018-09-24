package apc.android

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import apc.common.toJson
import java.util.*

open class Adapter<T>(private val itemRes: Int = 0, callback: DiffUtil.ItemCallback<T> = Callback()) : ListAdapter<T, Holder>(callback) {
    override fun getItemViewType(position: Int) = itemRes
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindTo((getItem(position)))
    }
}

open class PairAdapter<K, V>(itemRes: Int = 0) : Adapter<Pair<K, V>>(itemRes, PairCallback()) {
    @Suppress("unused")
    fun submitMap(map: Map<K, V>) {
        submitList(map.map { it.key to it.value })
    }
}

class FieldAdapter(bean: Any) : PairAdapter<CharSequence, CharSequence>(R.layout.item_field) {
    init {
        submitList((bean as? Class<*> ?: bean.javaClass).declaredFields.map {
            it.isAccessible = true
            var value = it[bean]
            if (value is Array<*>) value = Arrays.toString(value).run { substring(1, lastIndex) }
            it.name to (value as? CharSequence ?: value.toJson())
        })
    }
}

class Holder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindTo(item: Any?) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }
}

open class Callback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

class PairCallback<K, V> : Callback<Pair<K, V>>() {
    override fun areItemsTheSame(oldItem: Pair<K, V>, newItem: Pair<K, V>) = oldItem.first == newItem.first
}