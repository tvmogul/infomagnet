import { WebPlugin } from '@capacitor/core';
import { InfoMagnetPlugin } from './definitions';

export class InfoMagnetWeb extends WebPlugin implements InfoMagnetPlugin {
  constructor() {
    super({
      name: 'InfoMagnet',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const InfoMagnet = new InfoMagnetWeb();

export { InfoMagnet };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(InfoMagnet);
