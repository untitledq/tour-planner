import { MenuItem } from 'primeng/api';

export const SIDEBAR_NAV_ITEMS: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'pi pi-home',
    routerLink: '/dashboard'
  },
  {
    label: 'Tours',
    icon: 'pi pi-map',
    routerLink: 'null'
  },
  {
    label: 'Tour Logs',
    icon: 'pi pi-list-check',
    routerLink: 'null'
  },
  {
    label: 'Import/Export',
    icon: 'pi pi-file-export',
    routerLink: 'null'
  }
];
