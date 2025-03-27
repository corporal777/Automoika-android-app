package kg.autojuuguch.automoikakg.exceptions

class PermissionNotGrantedException(val permission: String? = null) : Exception()
class GPSNotEnabledException() : Exception()