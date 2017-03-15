import { APlanetaryOptimizerPage } from './app.po';

describe('a-planetary-optimizer App', () => {
  let page: APlanetaryOptimizerPage;

  beforeEach(() => {
    page = new APlanetaryOptimizerPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
