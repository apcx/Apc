@file:Suppress("UNCHECKED_CAST")

package apc.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.internal.impl.descriptors.ValueDescriptor
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.jvmName

val <T> KProperty0<T>.jClass
    get(): Class<T> {
        val receiver = (this as CallableReference).boundReceiver::class
        return cache.getOrPut("${receiver.jvmName}.$name") {
            Class.forName(DescriptorUtils.getFqName((getProperties(receiver, Name.identifier(name))
                    as List<ValueDescriptor>)[0].type.constructor.declarationDescriptor!!).asString())
        } as Class<T>
    }

// KClassImpl.getProperties()
private val getProperties by lazy { Any::class::class.java.getMethod("getProperties", Name::class.java) }
private val cache by lazy { mutableMapOf<String, Class<*>>() }

abstract class BindFragment<Binding : ViewDataBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: Binding
    protected lateinit var vm: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val cache = getOrPut(this::class.jvmName) { BindCache(::binding, ::vm) }
        binding = cache.inflate(null, inflater, container, false) as Binding
        binding.setLifecycleOwner(this)
        cache.owner?.set(binding, this)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val clazz = ::vm.jClass
        if (!Modifier.isAbstract(clazz.modifiers)) {
            vm = ViewModelProviders.of(this)[clazz]
            get(this::class.jvmName)!!.vm?.set(binding, vm)
        }
    }

    companion object : ArrayMap<String, BindCache>()
}

class BindCache(bindingProperty: KProperty0<ViewDataBinding>, vmProperty: KProperty0<ViewModel>) {

    internal val inflate: Method
    internal var owner: Field? = null
    internal var vm: Field? = null

    init {
        val bindingClass = bindingProperty.jClass
        val ownerClass = (bindingProperty as CallableReference).boundReceiver.javaClass
        val vmClass = vmProperty.jClass
        inflate = bindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
        val fields = bindingClass.declaredFields
        fields.find { it.type.isAssignableFrom(ownerClass) }?.let(::owner::set)
        fields.find { it.type.isAssignableFrom(vmClass) }?.let(::vm::set)
    }
}