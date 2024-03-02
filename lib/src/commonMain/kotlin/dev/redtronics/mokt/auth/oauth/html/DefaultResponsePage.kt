/*
 * MIT License
 * Copyright 2024 Redtronics
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package dev.redtronics.mokt.auth.oauth.html

import kotlinx.html.*

fun HTML.defaultResponsePage() {
    body {
        h1 {
            +"Authentication Successful!"
        }
        p {
            +"You can now close this tab and return to the application."
        }
        style {
            unsafe {
                raw(
                    """
                    * {
                        box-sizing: border-box;
                        margin: 0;
                        padding: 0;
                    }
                    body {
                        font-family: Arial, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        color: white;
                        flex-direction: column;
                        background: linear-gradient(45deg, #6200ff, #00ffd4, #6200ff, #00ffd4);
                        background-size: 400% 400%;
                        animation: gradientAnimation 20s infinite;
                    }
                    @keyframes gradientAnimation {
                        0% {
                            background-position: 0 50%;
                        }
                        50% {
                            background-position: 100% 50%;
                        }
                        100% {
                            background-position: 0 50%;
                        }
                    }
                    h1 {
                        text-align: center;
                    }
                    p {
                        text-align: center;
                    }
                    """.trimIndent()
                )
            }
        }
    }

}
