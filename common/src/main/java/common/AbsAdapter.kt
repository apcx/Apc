package common

import android.net.Uri
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.common.base.CaseFormat
import java.lang.reflect.Field

abstract class AbsAdapter<VH: RecyclerView.ViewHolder, M: Any>(@LayoutRes private val itemRes: Int, private val singleSelect: Boolean = false): RecyclerView.Adapter<VH>() {

    private lateinit var recyclerView: RecyclerView
    private val fields: SparseArray<Field?> by lazy(LazyThreadSafetyMode.NONE) { SparseArray<Field?>() }

    var itemList = mutableListOf<M>()
        @UiThread set(list) {
            field = list
            notifyDataSetChanged()
        }

    var selected = -1
        @UiThread set(position) {
            if (position != field) {
                val old = field
                field = position
                if (::recyclerView.isInitialized) {
                    val range = 0..(itemCount - 1)
                    if (old in range) notifyItemChanged(old)
                    if (position in range) {
                        notifyItemChanged(position)
                        recyclerView.smoothScrollToPosition(position)
                    }
                }
            }
        }

    final override fun onAttachedToRecyclerView(recyclerView: RecyclerView) { this.recyclerView = recyclerView }

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = onNewHolder(LayoutInflater.from(parent.context).inflate(itemRes, parent, false), viewType)
        if (singleSelect) holder.itemView.setOnClickListener {
            selected = holder.adapterPosition
            onItemClick(holder.adapterPosition, it.id)
        }
        return holder
    }

    protected abstract fun onNewHolder(itemView: View, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (holder.itemView !is ViewGroup) onBindViewItem(holder.itemView, itemList[position])
        if (singleSelect) holder.itemView.isSelected = position == selected
    }

    @UiThread protected open fun onBindViewItem(view: View, item: M) {
        var value: Any? = onConvertDataString(item, view.id)
        if (null == value && view.id > 0) {
            var field: Field? = null
            if (fields.indexOfKey(view.id) >= 0) field = fields[view.id]
            else {
                try {
                    field = item.javaClass.getDeclaredField(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, view.resources.getResourceEntryName(view.id)))
                    field.isAccessible = true
                } catch (e: NoSuchFieldException) { }
                fields.put(view.id, field)
            }
            value = field?.get(item)
        }
        if (value is Boolean) view.visibility = if (value) View.VISIBLE else View.GONE
        else when (view) {
            is TextView  -> view.text = if (value is CharSequence) value else value?.toString()
            is ImageView -> view.setImageURI(if (value != null) Uri.parse(value.toString()) else null)
        }
    }

    protected open fun onConvertDataString(item: M, id: Int): CharSequence? = if (id <= 0) item.toString() else null
    protected open fun onItemClick(position: Int, id: Int) { }
}

@Suppress("unused")
open class SmartAdapter<M: Any>(@LayoutRes itemRes: Int, singleSelect: Boolean): AbsAdapter<MapHolder, M>(itemRes, singleSelect) {

    constructor(@LayoutRes itemRes: Int): this(itemRes, false) // for java

    override fun onNewHolder(itemView: View, viewType: Int): MapHolder {
        val holder = MapHolder(itemView)
        holder.setup { onItemClick(holder.adapterPosition, it.id) }
        return holder
    }

    override fun onBindViewHolder(holder: MapHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder.itemView is ViewGroup) for (i in 0..(holder.views.size() - 1)) onBindViewItem(holder.views.valueAt(i), itemList[position])
    }
}

class MapHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    internal val views = SparseArray<View>()
    private var setup = false

    fun setup(view: View = itemView, listener: ((View) -> Unit)? = null) {
        setup = true
        if (view.id > 0)       views.put(view.id, view)
        if (view.isClickable)  view.setOnClickListener(listener)
        if (view is ViewGroup) for (i in 0..(view.childCount - 1)) setup(view.getChildAt(i), listener) // recursive
    }

    operator fun <V: View> get(@IdRes id: Int): V {
        var view = views[id]
        if (!setup && null == view) {
            view = itemView.findViewById(id)
            views.put(id, view)
        }
        @Suppress("UNCHECKED_CAST")
        return view as V
    }
}
