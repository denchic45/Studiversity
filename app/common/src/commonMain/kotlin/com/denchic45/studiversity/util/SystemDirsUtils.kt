package com.denchic45.studiversity.util

import okio.Path

 val SystemDirs.databasePath: Path
    get() = appDir / "databases" / "database.db"