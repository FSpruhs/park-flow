package com.spruhs.parkflow.architecture

import com.spruhs.parkflow.ParkFlowApplication
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModulithTests {
    @Test
    fun `verifies modular structure`() {
        val modules = ApplicationModules.of(ParkFlowApplication::class.java)
        modules.verify()
    }
}
