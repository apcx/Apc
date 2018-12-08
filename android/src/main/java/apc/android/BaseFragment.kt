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
import java.lang.reflect.Method
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.internal.impl.descriptors.ValueDescriptor
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.jvmName

val <T> KProperty<T>.jClass
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

open class BaseFragment<Binding : ViewDataBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: Binding
    protected lateinit var vm: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clazz = ::binding.jClass
        binding = getOrPut(clazz.name) {
            clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
        }(null, inflater, container, false) as Binding
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProviders.of(this)[::vm.jClass]
//        binding.setVariable(BR.vm, vm)
    }

    companion object : ArrayMap<String, Method>()
}