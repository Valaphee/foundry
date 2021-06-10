/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.inject.context

/**
 * @author Kevin Ludwig
 */
class ComponentCircularDependencyException(
    message: String? = null,
    cause: Throwable? = null
) : ComponentLinkException(message, cause)