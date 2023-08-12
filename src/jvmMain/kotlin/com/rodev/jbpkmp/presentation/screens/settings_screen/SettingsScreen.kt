package com.rodev.jbpkmp.presentation.screens.settings_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.LocalMutableLocale
import com.rodev.jbpkmp.LocalMutableTheme
import com.rodev.jbpkmp.data.ProgramDataRepositoryImpl
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.cancel
import com.rodev.jbpkmp.presentation.localization.language
import com.rodev.jbpkmp.presentation.localization.languageDescription
import com.rodev.jbpkmp.presentation.localization.openLastProject
import com.rodev.jbpkmp.presentation.localization.save
import com.rodev.jbpkmp.presentation.localization.supportedLocalesNow
import com.rodev.jbpkmp.presentation.localization.useDarkTheme
import com.rodev.jbpkmp.presentation.screens.settings_screen.components.BooleanProperty
import com.rodev.jbpkmp.presentation.screens.settings_screen.components.EnumProperty
import java.util.*

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    val viewModel = remember { SettingsScreenViewModel(ProgramDataRepositoryImpl()) }
    val localization = Vocabulary.localization

    val settings by remember { derivedStateOf { viewModel.settings } }
    var useDarkTheme by remember { mutableStateOf(settings.useDarkTheme) }
    var openLastProject by remember { mutableStateOf(settings.openLastProject) }
    var selectedLocale by remember { mutableStateOf(Locale(settings.languageCode)) }

    val localeSetter = LocalMutableLocale.current
    val darkThemeSetter = LocalMutableTheme.current

    val onDismissRequestWrapper = remember {
        {
            // Reset values
            localeSetter(Locale(settings.languageCode))
            darkThemeSetter(settings.useDarkTheme)
            onDismissRequest()
        }
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = localization.language(),
                    style = MaterialTheme.typography.h3
                )

                IconButton(
                    onClick = onDismissRequestWrapper
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            }

            // Locale
            EnumProperty(
                label = localization.languageDescription(),
                propertyText = selectedLocale.displayLanguage.replaceFirstChar(Char::titlecase),
            ) { state ->
                supportedLocalesNow.forEach {
                    DropdownMenuItem(
                        onClick = {
                            localeSetter(it)
                            selectedLocale = it
                            state.expanded = false
                        }
                    ) {
                        Text(it.displayLanguage.replaceFirstChar(Char::titlecase))
                    }
                }
            }

            Divider(Modifier.fillMaxWidth())

            // Theme
            BooleanProperty(
                modifier = Modifier.fillMaxWidth(),
                value = useDarkTheme,
                onCheckedChange = { useDarkTheme = it },
                label = localization.useDarkTheme()
            )

            Divider(Modifier.fillMaxWidth())

            // Open last project
            BooleanProperty(
                modifier = Modifier.fillMaxWidth(),
                value = openLastProject,
                onCheckedChange = { openLastProject = it },
                label = localization.openLastProject()
            )

            Divider(Modifier.fillMaxWidth())

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                val buttonWidth = 150.dp

                OutlinedButton(
                    onClick = onDismissRequestWrapper,
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(localization.cancel())
                }

                Spacer(Modifier.width(25.dp))

                Button(
                    onClick = {
                        SettingsScreenEvent.SaveSettings(
                            language = selectedLocale.language,
                            useDarkTheme = useDarkTheme,
                            openLastProject = openLastProject
                        ).let(viewModel::onEvent)

                        onDismissRequest()
                    },
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(localization.save())
                }
            }
        }
    }
}