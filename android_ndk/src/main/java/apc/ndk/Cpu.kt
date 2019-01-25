package apc.ndk

@Suppress("SpellCheckingInspection")
object Cpu {

    private const val ANDROID_CPU_FAMILY_ARM = 1
    private const val ANDROID_CPU_FAMILY_X86 = 2
    private const val ANDROID_CPU_FAMILY_MIPS = 3
    private const val ANDROID_CPU_FAMILY_ARM64 = 4
    private const val ANDROID_CPU_FAMILY_X86_64 = 5
    private const val ANDROID_CPU_FAMILY_MIPS64 = 6

    init {
        System.loadLibrary("cpu")
    }

    val features get() = "${family()}.${features()}"
    private external fun family(): Int
    private external fun features(): Int

    fun featureList(family: Int = family(), features: Int = features()) = when (family) {
        ANDROID_CPU_FAMILY_ARM -> ARM_FEATURES.filter(features)
        ANDROID_CPU_FAMILY_ARM64 -> ARM64_FEATURES.filter(features)
        ANDROID_CPU_FAMILY_X86, ANDROID_CPU_FAMILY_X86_64 -> X86_FEATURES.filter(features)
        ANDROID_CPU_FAMILY_MIPS, ANDROID_CPU_FAMILY_MIPS64 -> MIPS_FEATURES.filter(features)
        else -> emptyList()
    }

    private fun Array<String>.filter(features: Int) = filterIndexed { index, _ -> features and (1 shl index) != 0 }

    private val ARM_FEATURES = arrayOf(
            "ARM_FEATURE_ARMv7",
            "ARM_FEATURE_VFPv3",
            "ARM_FEATURE_NEON",
            "ARM_FEATURE_LDREX_STREX",
            "ARM_FEATURE_VFPv2",
            "ARM_FEATURE_VFP_D32",
            "ARM_FEATURE_VFP_FP16",
            "ARM_FEATURE_VFP_FMA",
            "ARM_FEATURE_NEON_FMA",
            "ARM_FEATURE_IDIV_ARM",
            "ARM_FEATURE_IDIV_THUMB2",
            "ARM_FEATURE_iWMMXt",
            "ARM_FEATURE_AES",
            "ARM_FEATURE_PMULL",
            "ARM_FEATURE_SHA1",
            "ARM_FEATURE_SHA2",
            "ARM_FEATURE_CRC32")

    private val ARM64_FEATURES = arrayOf(
            "ARM64_FEATURE_FP",
            "ARM64_FEATURE_ASIMD",
            "ARM64_FEATURE_AES",
            "ARM64_FEATURE_PMULL",
            "ARM64_FEATURE_SHA1",
            "ARM64_FEATURE_SHA2",
            "ARM64_FEATURE_CRC32")

    private val X86_FEATURES = arrayOf(
            "X86_FEATURE_SSSE3",
            "X86_FEATURE_POPCNT",
            "X86_FEATURE_MOVBE",
            "X86_FEATURE_SSE4_1",
            "X86_FEATURE_SSE4_2",
            "X86_FEATURE_AES_NI",
            "X86_FEATURE_AVX",
            "X86_FEATURE_RDRAND",
            "X86_FEATURE_AVX2",
            "X86_FEATURE_SHA_NI")

    private val MIPS_FEATURES = arrayOf(
            "MIPS_FEATURE_R6",
            "MIPS_FEATURE_MSA")
}