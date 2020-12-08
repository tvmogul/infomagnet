declare module '@capacitor/core' {
  interface PluginRegistry {
    InfoMagnet: InfoMagnetPlugin;
  }
}

export interface InfoMagnetPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
