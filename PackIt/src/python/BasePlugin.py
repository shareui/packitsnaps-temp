# pyright: reportMissingImports=false
from base_plugin import BasePlugin
from .Main import load, unload
from .ui.SettingsScreen import openSettingsScreen

class Plugin(BasePlugin):
    def on_plugin_load(self):
        load(self)

    def on_plugin_unload(self):
        unload(self)

    def create_settings(self):
        from ui.settings import Text
        return [Text("Compose Settings")]

    def open_settings(self, fragment) -> bool:
        return openSettingsScreen(parentFragment=fragment)
