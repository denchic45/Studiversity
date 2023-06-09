package com.denchic45.kts.util

import okio.Path

 val SystemDirs.databasePath: Path
    get() = appDir / "databases" / "database.db"