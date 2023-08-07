package com.rodev.jbpkmp.presentation.screens.settings_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    val viewModel = remember { SettingsScreenViewModel(ProgramDataRepositoryImpl()) }
    val localization = Vocabulary.localization

    val settings = viewModel.repository.load().settings

    var useDarkTheme by remember { mutableStateOf(settings.useDarkTheme) }
    var openLastProject by remember { mutableStateOf(settings.openLastProject) }

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
                    onClick = onDismissRequest
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            }

            Text(localization.languageDescription())

            Box {
                val width = 200.dp
                var dropdownExpanded by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { dropdownExpanded = !dropdownExpanded },
                    modifier = Modifier.width(width)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Vocabulary.localization.locale.displayLanguage.replaceFirstChar(Char::titlecase))

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.width(width)
                ) {
                    supportedLocalesNow.forEach {
                        val localeSetter = LocalMutableLocale.current

                        DropdownMenuItem(
                            onClick = {
                                localeSetter(it)
                                dropdownExpanded = false
                            }
                        ) {
                            Text(it.displayLanguage.replaceFirstChar(Char::titlecase))
                        }
                    }
                }
            }

            Divider(Modifier.fillMaxWidth())

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val themeSetter = LocalMutableTheme.current

                Text(
                    text = localization.useDarkTheme(),
                    style = MaterialTheme.typography.h3
                )

                Checkbox(
                    checked = useDarkTheme,
                    onCheckedChange = {
                        useDarkTheme = it
                        themeSetter(it)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary)
                )
            }

            Divider(Modifier.fillMaxWidth())

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = localization.openLastProject(),
                    style = MaterialTheme.typography.h3
                )

                Checkbox(
                    checked = openLastProject,
                    onCheckedChange = {
                        openLastProject = it
                    },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary)
                )
            }

            Divider(Modifier.fillMaxWidth())

            Row {
                val buttonWidth = 150.dp

                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(localization.cancel())
                }

                Spacer(Modifier.width(25.dp))

                Button(
                    onClick = {
                        SettingsScreenEvent.SaveSettings(
                            language = localization.locale.language,
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