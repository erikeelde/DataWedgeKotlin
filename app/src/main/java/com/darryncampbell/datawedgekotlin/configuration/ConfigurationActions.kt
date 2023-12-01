package com.darryncampbell.datawedgekotlin.configuration

interface ConfigurationActions {
    fun ean8(enabled: Boolean)
    fun ean13(enabled: Boolean)
    fun code39(enabled: Boolean)
    fun code128(enabled: Boolean)
    fun clearHistory()
    fun illumination(enabled: Boolean)
    fun picklist(enabled: Boolean)
}
