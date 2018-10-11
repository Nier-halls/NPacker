import os
import sys
import apk_helper


def open_apk(apk_file_path):
    if apk_file_path is None:
        return None

    return open(apk_file_path, 'rb')


file_path = "C:\\Develop\\Project\\Android\\GradlePlugin\\extension\\tzzb_1.30.0_4565_pre_debug.apk"
apk_file = open_apk(file_path)
apk_file_size = os.path.getsize(file_path)
eocd_offset = apk_helper.find_eocd_offset(apk_file, apk_file_size)
apk_helper.find_cdso(apk_file, eocd_offset)

print(sys.getdefaultencoding())
