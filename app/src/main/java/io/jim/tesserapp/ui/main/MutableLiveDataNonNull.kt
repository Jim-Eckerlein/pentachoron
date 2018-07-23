/*
 *  Created by Jim Eckerlein on 7/23/18 9:34 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:26 AM
 */

package io.jim.tesserapp.ui.main

import androidx.lifecycle.MutableLiveData

/**
 * A wrapper for [MutableLiveData] whose value can never be `null`.
 *
 * Additionally, [setValue] and [getValue] are thread safe.
 *
 * @constructor Creates a live-data with an initial value.
 */
open class MutableLiveDataNonNull<T : Any>(initialValue: T) : MutableLiveData<T>() {
    
    init {
        value = initialValue
    }
    
    /**
     * Return the current *non-null* value.
     */
    override fun getValue(): T {
        return synchronized(this) {
            super.getValue()!!
        }
    }
    
    /**
     * Set the *non-null* value to this live data.
     */
    override fun setValue(value: T) {
        synchronized(this) {
            super.setValue(value)
        }
    }
    
    /**
     * Install an immortal observer.
     *
     * @param callback Called upon value changes.
     */
    fun observeForeverNonNull(callback: (T) -> Unit) {
        synchronized(this) {
            super.observeForever {
                callback(it!!)
            }
        }
    }
    
}
