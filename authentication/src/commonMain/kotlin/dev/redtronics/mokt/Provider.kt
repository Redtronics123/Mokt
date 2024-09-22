/*
 * MIT License
 * Copyright 2024 Nils Jäkel
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

package dev.redtronics.mokt

/**
 * Central adapter for the authentication providers.
 *
 * @property name The name of the provider.
 *
 * @see Microsoft
 * @see Authentik
 * @see Keycloak
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public sealed class Provider {
    public abstract val name: String
}

/**
 * The Microsoft authentication provider.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public class Microsoft : Provider() {
    override val name: String
        get() = "Microsoft"
}

/**
 * The Authentik authentication provider.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public class Authentik : Provider() {
    override val name: String
        get() = "Authentik"
}

/**
 * The Keycloak authentication provider.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public class Keycloak : Provider() {
    override val name: String
        get() = "Keycloak"
}