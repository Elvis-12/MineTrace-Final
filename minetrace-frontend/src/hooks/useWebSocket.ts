import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8082';
const WS_URL = API_URL.replace(/^http/, 'ws') + '/ws/websocket';

const EVENT_MESSAGES: Record<string, string> = {
  MOVEMENT_RECORDED: 'New movement recorded on a batch',
  BATCH_UPDATED: 'A batch was updated',
  FRAUD_ANALYZED: 'Fraud analysis completed on a batch',
};

export function useWebSocket() {
  const queryClient = useQueryClient();
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    const client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe('/topic/updates', (message) => {
          try {
            const event = JSON.parse(message.body);
            const { type, batchId } = event;

            // Invalidate relevant caches so UI auto-refreshes
            queryClient.invalidateQueries({ queryKey: ['batches'] });
            queryClient.invalidateQueries({ queryKey: ['movements'] });

            if (batchId) {
              queryClient.invalidateQueries({ queryKey: ['batch', batchId] });
              queryClient.invalidateQueries({ queryKey: ['movements', batchId] });
            }

            if (type === 'FRAUD_ANALYZED') {
              queryClient.invalidateQueries({ queryKey: ['fraud'] });
            }

            const msg = EVENT_MESSAGES[type];
            if (msg) toast(msg, { icon: '🔄', duration: 3000 });
          } catch {
            // ignore malformed messages
          }
        });
      },
      onDisconnect: () => {},
      onStompError: () => {},
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [queryClient]);
}
