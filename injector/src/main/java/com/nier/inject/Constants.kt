package com.nier.inject

/**
 * Author fangguodong
 * Date   2018-08-19 4:46 PM
 * E-mail fangguodong@myhexin.com
 */

const val APK_SIGN_BLOCK_MAGIC_HIGH = 0x3234206b636f6c42L
const val APK_SIGN_BLOCK_MAGIC_LOW = 0x20676953204b5041L

// End of central directory record (EOCD)
// Offset    Bytes     Description[23]
// 0           4       End of central directory signature = 0x06054b50
// 4           2       Number of this disk
// 6           2       Disk where central directory starts
// 8           2       Number of central directory records on this disk
// 10          2       Total number of central directory records
// 12          4       Size of central directory (bytes)
// 16          4       Offset of start of central directory, relative to start of archive
// 20          2       Comment length (n)
// 22          n       Comment
const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50
const val END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE = 4
const val NUMBER_OF_THIS_DISK_BYTE_SIZE = 2
const val DISK_WHERE_CENTRAL_DIRECTORY_STARTS_BYTE_SIZE = 2
const val NUMBER_OF_CENTRAL_DIRECTORY_RECORDS_BYTE_SIZE = 2
const val TOTAL_NUMBER_OF_CENTRAL_DIRECTORY_RECORDS_BYTE_SIZE = 2
const val SIZE_OF_CENTRAL_DIRECTORY_BYTE_SIZE = 4
const val OFFSET_OF_START_OF_CENTRAL_DIRECTORY_BYTE_SIZE = 4
const val COMMENT_LENGTH_BYTE_SIZE = 2

// OFFSET       DATA TYPE  DESCRIPTION
// * @+0  bytes uint64:    size in bytes (excluding this field)
// * @+8  bytes payload
// * @-24 bytes uint64:    size in bytes (same as the one above)
// * @-16 bytes uint128:   magic
const val APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE = 8
const val APK_SIGN_BLOCK_SIZE_BYTE_SIZE = 8
const val SIGN_BLOCK_PAYLOAD_VALUE_LENGTH_BYTE_SIZE = 8
const val SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE = 4

const val APK_SIGN_V2_KEY = 0x7109871a
const val APK_EXTRA_MAGIC = "TZZB EXTRA DATAS"

const val DEFAULT_EXTRA_PAYLOAD_KEY =

