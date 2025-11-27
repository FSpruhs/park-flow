package com.spruhs.parkflow.parkinginventory

import org.springframework.modulith.ApplicationModule

@ApplicationModule(
    allowedDependencies = [
        "common :: common-es",
        "common :: common-configs",
        "common :: common-helper",
    ],
)
class ModuleMetadata
