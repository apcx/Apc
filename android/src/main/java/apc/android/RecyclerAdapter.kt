package apc.android

import android.content.Context
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
import apc.common.underscoresToCamelCase
import java.lang.reflect.Field

@Suppress("unused")
open class RecyclerAdapter<M: Any>(@LayoutRes private val itemRes: Int, private val singleSelect: Boolean) : RecyclerView.Adapter<MapHolder>() {

    constructor(itemRes: Int) : this(itemRes, false) // for java

    lateinit var context: Context
        private set

    var itemList = mutableListOf<M?>()
        @UiThread set(list) {
            field = list
            notifyDataSetChanged()
        }

    var item: M? = null
        @UiThread set(item) {
            field = item
            itemList = mutableListOf(item)
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var selectedItem: M? = null
        protected set

    open var selected = -1
        @UiThread set(position) {
            if (position != field) {
                val old = field
                field = position
                val range = 0 until itemCount
                if (old in range) notifyItemChanged(old)
                if (position in range) {
                    selectedItem = itemList[position]
                    notifyItemChanged(position)
                    if (::recyclerView.isInitialized) recyclerView.smoothScrollToPosition(position)
                } else selectedItem = null
            }
        }

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainHolder: MapHolder
    private val fields by lazy(LazyThreadSafetyMode.NONE) { SparseArray<Field?>() }

    final override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        context = recyclerView.context
        this.recyclerView = recyclerView
    }

    @UiThread fun attach(parent: ViewGroup) {
        context = parent.context
        mainHolder = onCreateViewHolder(parent, 0)
        if (itemList.isEmpty()) item = null // build default one-null-element list
        parent.addView(mainHolder.itemView)
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged()                                            { onBindViewHolder(mainHolder, 0) }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) { if (0 in positionStart until positionStart + itemCount) onChanged() }
        })
        notifyDataSetChanged()
    }

    override fun getItemCount() = itemList.size

    @LayoutRes protected open fun getLayout(viewType: Int) = itemRes

    protected open fun getItem(position: Int): Any? = if (position in 0 until itemList.size) itemList[position] else null

    protected open fun getValue(item: Any?, id: Int) = item?.let {
        if (id <= 0) it
        else {
            var field: Field? = null
            if (fields.indexOfKey(id) >= 0) field = fields[id]
            else {
                try {
                    field = it.javaClass.getDeclaredField(context.resources.getResourceEntryName(id).underscoresToCamelCase())
                    field.isAccessible = true
                } catch (e: NoSuchFieldException) {}
                fields.put(id, field)
            }
            field?.get(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapHolder {
        val holder = MapHolder(LayoutInflater.from(parent.context).inflate(getLayout(viewType), parent, false))
        holder.setup { onItemClick(holder.adapterPosition, it.id) }
        if (singleSelect) holder.itemView.setOnClickListener {
            selected = holder.adapterPosition
            onItemClick(holder.adapterPosition, it.id)
        }
        return holder
    }

    override fun onBindViewHolder(holder: MapHolder, position: Int) {
        val item = getItem(position)
        if (holder.itemView !is ViewGroup) onBindView(holder.itemView, item) else for (i in 0 until holder.views.size()) onBindView(holder.views.valueAt(i), item)
        holder.itemView.isSelected = position == selected
    }

    protected open fun onBindView(view: View, item: Any?) { onUpdateView(view, getValue(item, view.id)) }

    protected open fun onUpdateView(view: View, value: Any?) {
        view.run {
            if (value is Boolean) visibility = if (value) View.VISIBLE else View.GONE
            else when (this) {
                is TextView  -> text = value as? CharSequence ?: value?.toString()
                is ImageView -> if (value is Int) setImageResource(value) else setImageURI(if (value != null) Uri.parse(value.toString()) else null)
            }
        }
    }

    protected open fun onItemClick(position: Int, id: Int) {}
}

class MapHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal val views = SparseArray<View>()
    private var setup = false

    fun setup(view: View = itemView, listener: ((View) -> Unit)? = null) {
        if (view.id > 0)       views.put(view.id, view)
        if (view.isClickable)  view.setOnClickListener(listener)
        if (view is ViewGroup) for (i in 0 until view.childCount) setup(view.getChildAt(i), listener) // recursive
        setup = true
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