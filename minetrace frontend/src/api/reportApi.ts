import api from './axios';
import { mockBatches, mockMovements } from './mockData';
import { USE_MOCK } from './authApi';

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const reportApi = {
  getSummary: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(400);
      return {
        data: {
          totalBatches: mockBatches.length,
          totalWeight: mockBatches.reduce((s: number, b: any) => s + b.initialWeight, 0),
          activeMines: 3,
          flaggedBatches: mockBatches.filter((b: any) => b.status === 'FLAGGED').length,
          highRiskBatches: mockBatches.filter((b: any) => b.riskLevel === 'HIGH').length,
          totalMovements: mockMovements.length,
        }
      };
    }
    return api.get('/api/reports/summary', { params: filters });
  },
  getMineProduction: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(400);
      return { data: [{ mineName: 'Mine Alpha', totalBatches: 4, totalWeight: 8000 }] };
    }
    return api.get('/api/reports/mine-production', { params: filters });
  },
  getMineralDistribution: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(400);
      return { data: [{ mineralType: 'Coltan', totalBatches: 3, totalWeight: 6000, percentage: 75 }] };
    }
    return api.get('/api/reports/mineral-distribution', { params: filters });
  },
  getProduction: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(600);
      return { data: { batches: mockBatches } };
    }
    return api.get('/api/reports/production', { params: filters });
  },
  getMovement: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(600);
      return { data: { movements: mockMovements } };
    }
    return api.get('/api/reports/movement', { params: filters });
  },
  getCompliance: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(600);
      return { data: { unverifiedBatches: mockBatches.filter((b: any) => b.status === 'REGISTERED') } };
    }
    return api.get('/api/reports/compliance', { params: filters });
  },
  getRisk: async (filters?: any) => {
    if (USE_MOCK) {
      await delay(600);
      return { data: { highRiskBatches: mockBatches.filter((b: any) => b.riskLevel === 'HIGH') } };
    }
    return api.get('/api/reports/risk', { params: filters });
  },
};
