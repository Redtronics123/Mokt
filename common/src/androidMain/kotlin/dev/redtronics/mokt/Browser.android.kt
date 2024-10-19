/*
 * MIT License
 * Copyright 2024 Nils Jäkel & David Ernst
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

package dev.redtronics.mokt

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import dev.redtronics.mokt.exception.FeatureNotSupported
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public actual suspend fun openInBrowser(url: Url): Unit = throw FeatureNotSupported("In Android we need to open a url in the browser a context that is only gettable from the instance of the main app. Furthermore it is not possible to set the context as parameter, because the other native implementations don't support it. For that case, we implemented a other function called openInAndroidBrowser which can be used instead.")

/**
 * Opens a given [Url] in the default browser.
 *
 * @param context The context of the application.
 * @param url The url to open.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 * */
public suspend fun openInAndroidBrowser(url: Url, context: Context): Unit = withContext(Dispatchers.IO) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url.toString()))
}