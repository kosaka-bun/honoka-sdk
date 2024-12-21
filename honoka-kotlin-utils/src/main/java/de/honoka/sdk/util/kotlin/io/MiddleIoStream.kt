package de.honoka.sdk.util.kotlin.io

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

abstract class MiddleIoStream : Closeable {
    
    private inner class In : InputStream() {
        
        override fun read(): Int = this@MiddleIoStream.read()
        
        override fun read(b: ByteArray, off: Int, len: Int): Int = this@MiddleIoStream.read(b, off, len)
        
        fun superRead(b: ByteArray, off: Int, len: Int): Int = super.read(b, off, len)
        
        override fun available(): Int = this@MiddleIoStream.available()
        
        override fun close() {
            this@MiddleIoStream.doClose()
        }
    }
    
    private inner class Out : OutputStream() {
        
        override fun write(b: Int) {
            this@MiddleIoStream.write(b)
        }
        
        override fun write(b: ByteArray, off: Int, len: Int) {
            this@MiddleIoStream.write(b, off, len)
        }
        
        fun superWrite(b: ByteArray, off: Int, len: Int) {
            super.write(b, off, len)
        }
        
        override fun flush() {
            this@MiddleIoStream.flush()
        }
        
        override fun close() {
            this@MiddleIoStream.doClose()
        }
    }
    
    private val inputStream = In()
    
    private val outputStream = Out()
    
    @Volatile
    private var closed: Boolean = false
    
    fun asIn(): InputStream = inputStream
    
    fun asOut(): OutputStream = outputStream
    
    protected abstract fun read(): Int
    
    protected open fun read(b: ByteArray, off: Int, len: Int): Int = inputStream.superRead(b, off, len)
    
    open fun read(limit: Int = Int.MAX_VALUE): ByteArray {
        val count = available().let {
            if(limit > it) it else limit
        }
        val buffer = ByteArray(count)
        inputStream.read(buffer)
        return buffer
    }
    
    protected abstract fun write(b: Int)
    
    open fun write(b: ByteArray, off: Int, len: Int) {
        outputStream.superWrite(b, off, len)
    }
    
    abstract fun available(): Int
    
    fun isEmpty(): Boolean = available() < 1
    
    open fun flush() {}
    
    private fun doClose() {
        synchronized(this) {
            if(closed) return
            closed = true
        }
        close()
    }
    
    override fun close() {}
}
