package com.rodev.jbpkmp.domain.model.saveable_variable.scope

import com.rodev.jbpkmp.domain.model.saveable_variable.Variable

interface VariableScope {

    val variables: List<Variable>

}