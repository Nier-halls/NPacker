import time

end_of_central_directory_signature = 0x06054b50
end_of_central_directory_block_size = 4
central_directory_start_offset_offset_with_eocd = 16
central_directory_start_offset_size = 4


def find_eocd_offset(file, file_len):
    print("apk file = " + str(file.name) + ", size = " + str(file_len))
    start_time = time.time()
    print("start time = " + str(start_time))
    eocd_offset = -1
    for i in reversed(range(0, file_len - end_of_central_directory_block_size + 1)):
        file.seek(i)
        if int.from_bytes(file.read(end_of_central_directory_block_size), byteorder='little', signed=False) == end_of_central_directory_signature:
            print("eocd position = " + str(i))
            eocd_offset = i
            break
    end_time = time.time()
    print("find finished. end time = " + str(end_time) + ",  cost = " + str(end_time - start_time))
    return eocd_offset


def find_cdso(file, eocd_offset):
    file.seek(eocd_offset + central_directory_start_offset_offset_with_eocd)
    cdso = int.from_bytes(file.read(central_directory_start_offset_size), byteorder='little', signed=False)
    print("cdso = " + str(cdso))

def check_apk_sign_block():
    pass