package com.chuckerteam.chucker.internal.data.cache

import android.util.LruCache

public val mockApiCache: LruCache<String, MockApiPackage> = LruCache<String, MockApiPackage>(25)

public data class MockApiPackage(val responseCode: Int, val jsonStringBody: String)
