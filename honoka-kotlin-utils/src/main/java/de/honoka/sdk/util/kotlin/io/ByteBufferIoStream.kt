package de.honoka.sdk.util.kotlin.io

class ByteBufferIoStream(private val blocking: Boolean = false) : MiddleIoStream() {
    
    private val buffer = ArrayList<Byte>()
    
    @Volatile
    private var readPointer = 0
    
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    private val javaThis = this as Object
    
    private fun privateRead(): Byte? {
        if(buffer.isEmpty() || readPointer > buffer.lastIndex) {
            if(blocking) {
                waitForNotEmpty()
            } else {
                return null
            }
        }
        readPointer++
        return buffer[readPointer - 1]
    }
    
    @Synchronized
    override fun read(): Int = privateRead()?.toUByte()?.toInt() ?: -1
    
    @Synchronized
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
            val remainingBytes = subList(readPointer, buffer.size).toTypedArray()
            clear()
            addAll(remainingBytes)
        }
        readPointer = 0
        return count
    }
    
    @Synchronized
    override fun read(limit: Int): ByteArray {
        if(blocking) waitForNotEmpty()
        return super.read(limit)
    }
    
    @Synchronized
    override fun write(b: Int) {
        buffer.add(b.toByte())
        javaThis.notifyAll()
    }
    
    @Synchronized
    override fun write(b: ByteArray) {
        buffer.addAll(b.asList())
        javaThis.notifyAll()
    }
    
    override fun available(): Int = buffer.size - readPointer
    
    private fun waitForNotEmpty() {
        while(buffer.isEmpty() || readPointer > buffer.lastIndex) {
            javaThis.wait()
        }
    }
}
