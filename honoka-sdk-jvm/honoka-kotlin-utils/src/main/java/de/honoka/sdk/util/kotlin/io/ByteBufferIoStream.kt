package de.honoka.sdk.util.kotlin.io

import de.honoka.sdk.util.basic.javadoc.NotThreadSafe

@NotThreadSafe
class ByteBufferIoStream : MiddleIoStream() {
    
    private val buffer = ArrayList<Byte>()
    
    @Volatile
    private var readPointer = 0
    
    private fun privateRead(): Byte? {
        if(isEmpty()) return null
        readPointer++
        return buffer[readPointer - 1]
    }
    
    override fun read(): Int = privateRead()?.toUByte()?.toInt() ?: -1
    
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if(len < 1) return 0
        if(off + len > b.size) {
            throw IndexOutOfBoundsException("size: ${b.size}, off: $off, len: $len")
        }
        var count = 0
        while(count < len) {
            val aByte = privateRead() ?: break
            b[off + count] = aByte
            count++
            if(isEmpty()) break
        }
        if(count < 1) return -1
        buffer.run {
            subList(readPointer, size).toTypedArray().let {
                clear()
                addAll(it)
            }
        }
        readPointer = 0
        return count
    }
    
    override fun write(b: Int) {
        buffer.add(b.toByte())
    }
    
    override fun write(b: ByteArray, off: Int, len: Int) {
        if(len < 1) return
        if(off < 0 || off + len > b.size) {
            throw IndexOutOfBoundsException("size: ${b.size}, off: $off, len: $len")
        }
        val subArray = if(off == 0 && len == b.size) b else b.copyOfRange(off, off + len)
        buffer.addAll(subArray.asList())
    }
    
    override fun available(): Int = buffer.size - readPointer
}
