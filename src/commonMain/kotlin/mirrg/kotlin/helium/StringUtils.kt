package mirrg.kotlin.helium

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    if (maxLength <= 0) return suffix
    return if (this.length <= maxLength) this else this.take(maxLength) + suffix
}
