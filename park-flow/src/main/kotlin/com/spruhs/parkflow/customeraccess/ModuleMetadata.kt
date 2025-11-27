package com.spruhs.parkflow.customeraccess

import org.springframework.modulith.ApplicationModule

@ApplicationModule(
    allowedDependencies = [
        "common :: common-es",
        "common :: common-configs",
        "common :: common-helper",
        "parkinginventory :: parking-inventory-api",
    ],
)
class ModuleMetadata
