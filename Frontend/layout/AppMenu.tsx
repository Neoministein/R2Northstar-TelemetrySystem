import React from 'react';
import AppMenuitem from './AppMenuitem';
import {MenuProvider} from './context/menucontext';

const AppMenu = () => {
    const model = [
        {
            label: 'Home',
            items: [{ label: 'Home', icon: 'pi pi-fw pi-home', to: '/' }]
        },
        {
            label: 'Matches',
            items: [
                { label: 'Live Matches', icon: 'pi pi-fw pi-search', to: '/live/match' },
                { label: 'Ended Matches', icon: 'pi pi-fw pi-history', to: '/finished/match' }
            ]
        },
        {
            label: 'Global Stats',
            items: [
                { label: 'Maps', icon: 'pi pi-fw pi-map', to: '/stats/global/map' }
            ]
        },
        {
            label: 'Global Player Stats',
            items: [
                { label: 'Player Kills', icon: 'pi pi-fw pi-user', to: '/stats/global/player/player-kills' },
                { label: 'Npc Kills', icon: 'pi pi-fw pi-android', to: '/stats/global/player/npc-kills' },
                { label: 'K/D', icon: 'pi pi-fw pi-users', to: '/stats/global/player/player-kd' },
                { label: 'Wins', icon: 'pi pi-fw pi-exclamation-circle', to: '/stats/global/player/wins' },
                { label: 'Win Ratio', icon: 'pi pi-fw pi-percentage', to: '/stats/global/player/win-ratio' },
            ]
        },
        {
            label: 'Help / Issues',
            items: [
                {
                    label: 'View Source',
                    icon: 'pi pi-fw pi-code',
                    url: 'https://github.com/Neoministein/R2Northstar-TelemetrySystem',
                    target: '_blank'
                }
            ]
        }
    ];

    return (
        <MenuProvider>
            <ul className="layout-menu">
                {model.map((item, i) => {
                    return <AppMenuitem item={item} root={true} index={i} key={item.label} />;
                })}
            </ul>
        </MenuProvider>
    );
};

export default AppMenu;
